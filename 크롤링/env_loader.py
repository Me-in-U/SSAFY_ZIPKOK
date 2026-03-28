import os
from pathlib import Path


_LOADED = False


def load_env():
    global _LOADED
    if _LOADED:
        return

    env_path = Path(__file__).with_name(".env")
    if not env_path.exists():
        _LOADED = True
        return

    for raw_line in env_path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue

        key, value = line.split("=", 1)
        key = key.strip()
        value = value.strip()
        if len(value) >= 2 and value[0] == value[-1] and value[0] in {"'", '"'}:
            value = value[1:-1]
        os.environ.setdefault(key, value)

    _LOADED = True


def get_env(name: str, default=None):
    load_env()
    return os.environ.get(name, default)


def require_env(name: str):
    value = get_env(name)
    if value is None or value == "":
        raise RuntimeError(f"{name} 환경변수가 필요합니다. 크롤링/.env 파일을 확인하세요.")
    return value
