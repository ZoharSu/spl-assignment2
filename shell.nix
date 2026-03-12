{pkgs ? import <nixpkgs> {}, ...}:

with pkgs; mkShell {
	nativeBuildInputs = [ maven jdt-language-server openjdk ];
}
