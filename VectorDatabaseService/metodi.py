from fastapi import FastAPI, Query, HTTPException
from contextlib import asynccontextmanager
from py_eureka_client import eureka_client
from pymilvus import Collection, connections
from sentence_transformers import SentenceTransformer
import uvicorn
import os
import socket
from config import connect_milvus, COLLECTION_PRODUCTS, COLLECTION_USERS
from pydantic import BaseModel

transformer = SentenceTransformer('all-MiniLM-L6-v2')

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(('10.254.254.254', 1))
        ip = s.getsockname()[0]
    except Exception:
        ip = '127.0.0.1'
    finally:
        s.close()
    return ip

async def init_eureka():
    await eureka_client.init_async(
        eureka_server="http://eureka-server:8761/eureka",
        app_name="vector-database-service",
        instance_host=os.getenv("INSTANCE_HOST", get_ip()),
        instance_port=8000
    )

@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Starting up vector service")

    connect_milvus()
    await init_eureka()

    try:
        Collection(COLLECTION_PRODUCTS).load()
        Collection(COLLECTION_USERS).load()
        print("Collcetions loaded into memory")
    except Exception as e:
        print(f"Collections not found ({e}). Run shop.py first")

    yield

    print("Shutting down")
    try:
        connections.disconnect("default")
    except:
        pass

app = FastAPI(lifespan=lifespan)

#http://localhost:9003/vector-database-service/search/complex-filter?query=shoes&brand=Nike&max_price=150
@app.get("/search/complex-filter")
def search_filtered(query: str, brand: str, max_price: float):
    col = Collection(COLLECTION_PRODUCTS)
    vec = transformer.encode(query).tolist()

    expr = f"brand == '{brand}' && price < {max_price}"

    res = col.search(
        data=[vec],
        anns_field="embedding",
        param={"metric_type": "L2", "params": {"nprobe": 10}},
        limit=5,
        expr=expr,
        output_fields=["name", "price", "brand"]
    )

    return [hit.entity.to_dict() for hit in res[0]]

#http://localhost:9003/vector-database-service/search/iterator?query=equipment&category=Gym
@app.get("/search/iterator")
def search_iterator(query: str, category: str):
    col = Collection(COLLECTION_PRODUCTS)
    vec = transformer.encode(query).tolist()

    iterator = col.search_iterator(
        data=[vec],
        anns_field="embedding",
        param={"metric_type": "L2", "params": {"nprobe": 10}},
        batch_size=5,
        limit=20,
        expr=f"category == '{category}'",
        output_fields=["name", "category"]
    )

    results = []
    while True:
        batch = iterator.next()
        if not batch: break
        for hit in batch:
            results.append({"id": hit.id, "name": hit.entity.get("name")})
    
    return {"total_retrieved": len(results), "data": results}

#http://localhost:9003/vector-database-service/search/hybrid?user_id=8084&query=best
@app.get("/search/hybrid")
def search_hybrid(user_id: int, query: str):
    col_user = Collection(COLLECTION_USERS)
    col_prod = Collection(COLLECTION_PRODUCTS)

    user_res = col_user.query(expr=f"user_id == {user_id}", output_fields=["pref_embedding"])
    if not user_res:
        raise HTTPException(404, f"user profile with ID {user_id} not found")
    
    user_vec = user_res[0]["pref_embedding"]
    text_vec = transformer.encode(query).tolist()

    hybrid_vec = [(t * 0.7 + u * 0.3) for t, u in zip(text_vec, user_vec)]

    res = col_prod.search(
        data=[hybrid_vec], 
        anns_field="embedding", 
        param={"metric_type": "L2", "params": {"nprobe": 10}}, 
        limit=5, 
        output_fields=["name", "category"]
    )

    return [hit.entity.to_dict() for hit in res[0]]

class UserCreateRequest(BaseModel):
    user_id: int
    username: str
    preferred_sport: str
    fitness_level: str

@app.post("/users")
def create_user_vector(user: UserCreateRequest):
    print(f">>> SAGA request recived: Creating vector for user {user.user_id}")
    try:
        col_user = Collection(COLLECTION_USERS)
        desc = f"I am a {user.fitness_level} athlete who loves {user.preferred_sport}."
        vector = transformer.encode(desc).tolist()
        data = [
            [user.user_id],
            [user.username],
            [user.preferred_sport],
            [user.fitness_level],
            [vector]
        ]

        col_user.insert(data)
        col_user.flush()

        print(f">>> SUCCESS: User {user.user_id} synced to Milvus.")
        return {"status": "success", "user_id": user.user_id}
        
    except Exception as e:
        print(f"!!! SAGA ERROR: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)