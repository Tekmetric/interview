import { Feature, View } from "ol";
import OlMap from "ol/Map";
import { Point } from "ol/geom";
import { transform } from "ol/proj";
import { Location } from "../types/Location";
import {
  ASSETS_PROJECTION,
  BASE_LAYER_SOURCE,
  CENTER_ANIMATION_DURATION,
  DEFAULT_CENTER_POINT,
  DEFAULT_ICON_SIZE,
  DEFAULT_MAP_PROJECTION,
  DEFAULT_ZOOM_FACTOR
} from "../constants/map.constants";
import Style from "ol/style/Style";
import Icon from "ol/style/Icon";
import DEFAULT_ASSET_ICON from "../assets/panda-pin.png";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { Attribution } from "ol/control";
import TileLayer from "ol/layer/Tile";
import BaseLayer from "ol/layer/Base";
import { defaults } from "ol/interaction/defaults";
import { Coordinate } from "ol/coordinate";

const mapDatasetAssetsToLayers = (assets: Location[]) => {
  const positionalFeatures: Feature<Point>[] = assets.map(asset =>
    mapPositionalAssetToFeature(asset)
  );

  return buildPointVectorLayerFromFeatures(positionalFeatures);
};

const mapPositionalAssetToFeature = (location: Location) => {
  const feature = new Feature({
    geometry: new Point(
      transform(
        [location.lon, location.lat],
        ASSETS_PROJECTION,
        DEFAULT_MAP_PROJECTION
      )
    )
  });

  feature.setStyle(new Style({
    image: new Icon({
      src: DEFAULT_ASSET_ICON,
      height: DEFAULT_ICON_SIZE.height,
      width: DEFAULT_ICON_SIZE.width,
    }),
  }));

  return feature;
}

const buildPointVectorLayerFromFeatures = (features: Feature<Point>[]) => {
  return new VectorLayer({
    source: new VectorSource({ features }),
    properties: {
      projection: DEFAULT_MAP_PROJECTION,
      id: 'rp_sightings',
    },
    visible: true,
  });
};

const buildAttribution = () => new Attribution({
  collapsible: true,
  attributions: `<a href="https://openlayers.org"></a>`,
});

const buildBaseLayer = () => new TileLayer({ source: new BASE_LAYER_SOURCE() });

const buildMap = (target: HTMLDivElement, withSelectLocation?: boolean) => {
  const mapLayers: BaseLayer[] = [buildBaseLayer()];
  const attributions = buildAttribution();

  const transformedCenterPoint = transform(
    DEFAULT_CENTER_POINT,
    ASSETS_PROJECTION,
    DEFAULT_MAP_PROJECTION
  );

  if (withSelectLocation) {
    const selectedLocationLayer = buildPointVectorLayerFromFeatures([]);
    mapLayers.push(selectedLocationLayer);
  }

  return new OlMap({
    target: target,
    layers: mapLayers,
    view: new View({
      projection: DEFAULT_MAP_PROJECTION,
      center: transformedCenterPoint,
      zoom: DEFAULT_ZOOM_FACTOR,
    }),
    controls: [attributions],
    interactions: defaults({
      doubleClickZoom: false,
    }),
  });
};

export const handleCenterMapOnPoint = (
  map: OlMap | undefined,
  centerPoint: Location | undefined,
  zoom?: boolean
) => {
  try {
    if (centerPoint && map) {
      const transformedCoordinate: Coordinate = transform(
        [centerPoint.lon, centerPoint.lat],
        ASSETS_PROJECTION,
        DEFAULT_MAP_PROJECTION
      );

      map?.getView().animate({
        duration: CENTER_ANIMATION_DURATION,
        center: transformedCoordinate,
        zoom: zoom ? DEFAULT_ZOOM_FACTOR : undefined,
      });
    }
  } catch (error) {
    console.log("Invalid location format! ---> ", error);
  }
};

const transformCoordinateFromMapProjection = (coordinate: Coordinate) => transform(
  coordinate,
  DEFAULT_MAP_PROJECTION,
  ASSETS_PROJECTION
);

const mapCoordinateToLocation = (coordinate: Coordinate) => {
  const transformedCoordinate = transform(
    coordinate,
    DEFAULT_MAP_PROJECTION,
    ASSETS_PROJECTION
  );

  return { lon: transformedCoordinate[0], lat: transformedCoordinate[1] };
};


const registerLocationClickEvent = (
  map: OlMap | undefined,
  onAssetsClick: (targetLocation: Location) => void | undefined,
): (() => void) => {
  if (map) {
    map.on("click", onAssetsClick);

    return () => {
      map.un("click", onAssetsClick);
    };
  }

  return () => {};
};

const formatLocationForDisplay = (location: Location): string => {
  return `${Math.abs(location.lat)}°${location.lat > 0 ? "N" : "S"}, ${Math.abs(location.lon)}°${location.lon > 0 ? "E" : "W"}`;
}

export const MapService = {
  mapDatasetAssetsToLayers,
  buildMap,
  handleCenterMapOnPoint,
  registerLocationClickEvent,
  mapCoordinateToLocation,
  formatLocationForDisplay
}
