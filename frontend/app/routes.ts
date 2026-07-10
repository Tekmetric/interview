import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("forecast/:locationId", "routes/forecast.tsx")
] satisfies RouteConfig;