# backend Helm chart

## Install

```bash
helm upgrade --install backend ./charts/backend \
  --namespace backend \
  --create-namespace \
  --set image.repository=${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/backend \
  --set image.tag=${IMAGE_TAG} \
  --wait --timeout 5m \
  --atomic
```

## EKS notes

- `service.type` defaults to `LoadBalancer`, which works well for a simple public endpoint on EKS.
- If you use the AWS Load Balancer Controller, switch to `Ingress` and set `ingress.enabled=true` with the appropriate annotations.
- The chart mounts writable `emptyDir` volumes at `/app/tmp` and `/app/tomcat` to match the non-root Java runtime.
