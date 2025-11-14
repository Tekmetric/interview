resource "spot_cloudspace" "loosh_dev" {
  cloudspace_name = "loosh-dev"
  region          = "us-central-ord-1"
  hacontrol_plane = false
  cni             = "calico"
}

resource "spot_spotnodepool" "default" {
  cloudspace_name = "loosh-dev"

  server_class = "gp.vs1.large-ord"
  bid_price    = "0.03"
  autoscaling = {
    min_nodes = 3
    max_nodes = 10
  }
}

data "spot_kubeconfig" "loosh_dev" {
  cloudspace_name = resource.spot_cloudspace.loosh_dev.name
}

resource "local_sensitive_file" "foo" {
  content  = data.spot_kubeconfig.loosh_dev.raw
  filename = "/Users/chazu/.kube/config"
}
