from pymilvus import connections

MILVUS_HOST = 'standalone'
MILVUS_PORT = '19530'

COLLECTION_PRODUCTS = 'products_v2'
COLLECTION_USERS = 'users_v2'

DIMENSION = 384

def connect_milvus():
    try:
        connections.connect(alias="default", host=MILVUS_HOST, port=MILVUS_PORT)
        print(f"--- Connected to Milvus at {MILVUS_HOST}:{MILVUS_PORT} ---")
    except Exception as e:
        print(f"Connection failed: {e}")
        