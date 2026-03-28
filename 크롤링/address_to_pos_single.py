import requests

from env_loader import require_env

apiurl = "https://api.vworld.kr/req/address?"
params = {
    "service": "address",
    "request": "getcoord",
    "crs": "epsg:4326",
    "address": "사상구 덕포동 789",
    "format": "json",
    "type": "PARCEL",
    "key": require_env("VWORLD_API_KEY"),
}
response = requests.get(apiurl, params=params)
if response.status_code == 200:
    print(response.json())
