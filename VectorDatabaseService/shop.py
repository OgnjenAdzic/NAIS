import random
from pymilvus import (
    FieldSchema, CollectionSchema, DataType, Collection, utility
)
from sentence_transformers import SentenceTransformer
from config import connect_milvus, COLLECTION_PRODUCTS, COLLECTION_USERS, DIMENSION

connect_milvus()

transformer = SentenceTransformer("all-MiniLM-L6-v2")

def create_and_seed():
    if utility.has_collection(COLLECTION_PRODUCTS):
        utility.drop_collection(COLLECTION_PRODUCTS)

    fields_prod = [
        FieldSchema(name="id", dtype=DataType.INT64, is_primary=True, auto_id=True),
        FieldSchema(name="name", dtype=DataType.VARCHAR, max_length=100),
        FieldSchema(name="category", dtype=DataType.VARCHAR, max_length=100),
        FieldSchema(name="brand", dtype=DataType.VARCHAR, max_length=100),
        FieldSchema(name="price", dtype=DataType.FLOAT),
        FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=DIMENSION)
    ]
    schema_prod = CollectionSchema(fields_prod, "Product Item Collection")
    col_prod = Collection(COLLECTION_PRODUCTS, schema_prod)

    #dobro pitanje kakve sve parametre imamo za index i za create index, sta znaci sta metric_type etc i sta je params i kako se menja ovo 128 u odnosu na ostalo
    col_prod.create_index("embedding", {
        "metric_type":"L2", #euklidsko rast takodje moze IP inner product i COSINE
        "index_type": "IVF_FLAT", #invert field index klasterujemo kako bi pretraga bila brza
        "params": {"nlist": 128} #broj klastera koji pravimo veci br klas manje pred u klasteru preporuka 4xsqrt(N) = 63 mi smo stavili vise kao primer
    })

    print("Seeding products")
    categories = ["Running", "Gym", "Swimming", "Cycling", "Hiking"]
    brands = ["Nike", "Adidas", "Puma", "Reebok", "NorthFace"]

    #milvus prima kolonu po kolonu umesto klasicnih red po red
    data_prod = [[], [], [], [], []]

    for i in range(250):
        cat = random.choice(categories)
        brand = random.choice(brands)
        name = f"{brand} {cat} Item {i}"
        desc = f"High quality {cat} gear from {brand}"

        data_prod[0].append(name)
        data_prod[1].append(cat)
        data_prod[2].append(brand)
        data_prod[3].append(random.uniform(20.0, 500.0))
        data_prod[4].append(transformer.encode(desc).tolist())

    col_prod.insert(data_prod)
    col_prod.flush()
    print(f"Inserted {col_prod.num_entities} products.")

    
    if utility.has_collection(COLLECTION_USERS):
        utility.drop_collection(COLLECTION_USERS)

    fields_user = [
        FieldSchema(name="user_id", dtype=DataType.INT64, is_primary=True),
        FieldSchema(name="username", dtype=DataType.VARCHAR, max_length=100),
        FieldSchema(name="preferred_sport", dtype=DataType.VARCHAR, max_length=100),
        FieldSchema(name="level", dtype=DataType.VARCHAR, max_length=50),
        FieldSchema(name="pref_embedding", dtype=DataType.FLOAT_VECTOR, dim=DIMENSION),
    ]

    schema_user = CollectionSchema(fields_user, "User preference profiles")
    col_user = Collection(COLLECTION_USERS, schema_user)

    col_user.create_index("pref_embedding", {
        "metric_type": "L2",
        "index_type": "IVF_FLAT",
        "params": {"nlist": 128}
    })

    print("Seeding users")
    levels = ["Beginer", "Intermediate", "Advanced"]
    data_user = [[], [], [], [], []]

    for i in range(1, 250):
        sport = random.choice(categories)
        data_user[0].append(i)
        data_user[1].append(f"user_{i}")
        data_user[2].append(sport)
        data_user[3].append(random.choice(levels))
        data_user[4].append(transformer.encode(f"I love {sport} and I need equipment.").tolist())
    
    col_user.insert(data_user)
    col_user.flush()
    print("------database seeding complete----")

if __name__ == "__main__":
    create_and_seed()