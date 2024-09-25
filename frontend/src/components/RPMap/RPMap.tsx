import OlMap from "ol/Map";
import { useCallback, useEffect, useLayoutEffect, useRef, useState } from "react";
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

    const map = MapService.buildMap(mapElement.current!, props.withSelectLocation);
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

  useEffect(() => {
    if (props.withSelectLocation, props.onSelectLocation) {
      const unregister = MapService.registerLocationClickEvent(
        map, 
        handleMapClick
      );

      return () => unregister();
    }
  }, [map, props.withSelectLocation, props.onSelectLocation]);
  
  const handleMapClick = useCallback(
    (event: any) => {
      const clickedLocation = mapRef.current?.getCoordinateFromPixel(
        event.pixel
      );

      if (clickedLocation) {
        const location = MapService.mapCoordinateToLocation(clickedLocation);
        props.onSelectLocation?.(location);
      }
    },
    [map, props.onSelectLocation]
  );

  return (
    <MapHost
      ref={mapElement}
      style={{ cursor: props.isLoading ? "wait" : "auto" }}
    />
  );
}
