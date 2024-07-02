import React, { useEffect, useRef, useState } from 'react';
import Art, { type ArtObject } from "./components/ArtObject.tsx";

const OBJECTS_TO_FETCH = 20;

const App = () => {
  const observerTarget = useRef(null);
  const [isLoading, setIsLoading] = useState(false);
  const [artObjects, setArtObjects] = useState<ArtObject[]>([]);
  const [start, setStart] = useState(0);

  const fetchObjectIds = async () => {
    const response = await fetch("https://collectionapi.metmuseum.org/public/collection/v1/search?isHighlight=true&hasImages=true&isOnView=true&q=french");
    const data = await response.json();

    return data;
  };

  const fetchObjects = async (startObjectIndex: number, objects: number[]) => {
    setIsLoading(true);
    try {
      const promises: Promise<Response>[] = [];
      for (let i = startObjectIndex; i < startObjectIndex + OBJECTS_TO_FETCH && i < objects.length; i++) {
        promises.push(fetch(`https://collectionapi.metmuseum.org/public/collection/v1/objects/${objects[i]}`));
      }
      Promise.all(promises)
        .then(results => {
          setIsLoading(false);
          results.forEach(async (result) => {
            const data = await result.json();
            setArtObjects((prev) => [...prev, data]);
          });
        });
    } catch (e) {
      setIsLoading(false);
      console.log(e);
    }
  };

  useEffect(() => {
    fetchObjectIds().then(data => {
      fetchObjects(start, data.objectIDs);
    });
  }, [start]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting) {
          setStart((prev) => prev + OBJECTS_TO_FETCH);
        }
      },
      { threshold: 1 }
    );

    if (observerTarget.current) {
      observer.observe(observerTarget.current);
    }

    return () => {
      if (observerTarget.current) {
        observer.unobserve(observerTarget.current);
      }
    };
  }, [observerTarget]);

  return (
    <div className="container mx-auto py-4">
      <h1 className="text-4xl font-bold tracking-wide text-red-800 mb-2">The Metropolitan Museum of Art</h1>
      <h4 className="text-lg tracking-wide font-light mb-4">
        The following items are french pieces of artwork that are currently available to be seen, in person, at The Met.
      </h4>
      <div className="grid grid-cols-2 gap-2">
        {
          Object
            .values<ArtObject>(artObjects)
            // If theres no image, its not really fun to render so we will skip it
            .filter((art: ArtObject) => art.primaryImageSmall !== "")
            .map(
              (art: ArtObject) => {
                return (<div key={`${art.objectID}`}><Art artObject={art} /></div>);
              })
        }
      </div>
      {isLoading && <div>Loading...</div>}
      <div ref={observerTarget}></div>
    </div>
  );
};
export default App;
