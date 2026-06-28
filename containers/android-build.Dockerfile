FROM ghcr.io/cirruslabs/android-sdk:35

WORKDIR /workspace

COPY . .

RUN ./gradlew --no-daemon test assembleDebug
