variable "REPO" { default = "" }
variable "IMAGE" { default = "" }
variable "GIT_SHA" { default = "" }
variable "GIT_SHORTSHA" { default = "" }
variable "VERSION" { default="latest" }
variable "GIT_BRANCH" { default = "" }

group "default" {
    targets = ["multiarch"]
}

target "meta" {
    tags = [ for TAG in [ GIT_SHA, GIT_SHORTSHA, VERSION, GIT_BRANCH == "master" ? "latest" : GIT_BRANCH ] : lower("${REPO}/${IMAGE}:${TAG}") if TAG != "" ]
    args = {
        VERSION = "${VERSION}"
    }
}

target "multiarch" {
    inherits = ["meta"]
    platforms = ["linux/amd64", "linux/arm64"]
}

target "local" {
    inherits = ["meta"]
    platforms = ["local"]
}

target "test-src-change" {
    inherits = ["local"]
    args = {
        TEST_SRC_CHANGE=uuidv4()
    }
}

target "test-dep-change" {
    inherits = ["local"]
    args = {
        TEST_DEP_CHANGE=uuidv4()
    }
}

target "dist" {
    platforms = ["local"]
    target = "jar"
    output = ["./dist"]
    args = {
        VERSION = "${VERSION}"
    }
}
