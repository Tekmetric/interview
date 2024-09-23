import OlMap from "ol/Map";
import { useEffect, useLayoutEffect, useRef, useState } from "react";
import { IRPMapProps } from "./RPMap.interface";
import { MapService } from "../../service/MapService";
import { MapHost } from "./RPMap.style";
import "./RPMap.css";
import VectorLayer from "ol/layer/Vector";

export default function RPMap (props: IRPMapProps) {
  const [map, setMap] = useState<OlMap>();
  const [featureLayer, setFeatureLayer] = useState<VectorLayer>();

  const mapElement = useRef<HTMLDivElement>(null);
  const mapRef = useRef<OlMap>();

  useLayoutEffect(() => {
    if (mapRef.current) {
      return
    }

    const map = MapService.buildMap(mapElement.current!);
    setMap(map);
    mapRef.current = map;
  }, []);

  useEffect(() => {
    featureLayer && map?.removeLayer(featureLayer);

    const assetLayer = MapService.mapDatasetAssetsToLayers(props.assets);

    map?.addLayer(assetLayer);
    setFeatureLayer(assetLayer);
  }, [map, props.assets]);

  useEffect(() => {
    MapService.handleCenterMapOnPoint(map, props.centerPoint);
  }, [map, props.centerPoint]);

  return (
    <MapHost
      ref={mapElement}
      style={{ cursor: props.isLoading ? "wait" : "auto" }}
    />
  );
}
