import requests

apiurl = "https://api.vworld.kr/req/address?"
params = {
    "service": "address",
    "request": "getcoord",
    "crs": "epsg:4326",
    "address": "사상구 덕포동 789",
    "format": "json",
    "type": "PARCEL",
    "key": "replace-with-vworld-api-key",
}
response = requests.get(apiurl, params=params)
if response.status_code == 200:
    print(response.json())
