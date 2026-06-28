{
  description = "Pressure VR Android development shell";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            allowUnfree = true;
            android_sdk.accept_license = true;
          };
        };

        android = pkgs.androidenv.composeAndroidPackages {
          platformVersions = [ "35" ];
          buildToolsVersions = [ "35.0.1" ];
          includeEmulator = false;
          includeSystemImages = false;
          includeSources = false;
        };

        androidSdk = android.androidsdk;
      in
      {
        devShells.default = pkgs.mkShell {
          packages = [
            androidSdk
            pkgs.gradle
            pkgs.jdk17
          ];

          ANDROID_HOME = "${androidSdk}/libexec/android-sdk";
          ANDROID_SDK_ROOT = "${androidSdk}/libexec/android-sdk";
          JAVA_HOME = "${pkgs.jdk17}";

          shellHook = ''
            echo "Pressure Android dev shell"
            echo "ANDROID_HOME=$ANDROID_HOME"
            echo "Run: scripts/pressure_check.sh"
          '';
        };
      });
}
