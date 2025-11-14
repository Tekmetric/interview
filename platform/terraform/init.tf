terraform {
  required_providers {
    porkbun = {
      source  = "kyswtn/porkbun"
      version = "0.1.2"
    }

    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.46.0"
    }
    spot = {
      source = "rackerlabs/spot"
    }
  }
}

provider "digitalocean" {

}

provider "spot" {
}
