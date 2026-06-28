FROM ghcr.io/cirruslabs/android-sdk:35

WORKDIR /workspace

COPY . .

RUN scripts/pressure_check.sh
