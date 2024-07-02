import React from "react";

export interface ArtObject {
    "objectID": number;
    "isPublicDomain": boolean;
    "primaryImage": string;
    "primaryImageSmall": string;
    "additionalImages": string[];
    "department": string;
    "title": string;
    "artistDisplayName": string;
    "artistWikidata_URL": string;
    "artistULAN_URL": string;
    "objectDate": string;
    "medium": string;
}
const Component = ({ artObject }: { artObject: ArtObject; }) => {
    if (!artObject || !artObject.title) {
        return null;
    }

    let Artist = () => <p className="text-md font-thin tracking-tight">{artObject.artistDisplayName}</p>;
    if (artObject.artistWikidata_URL || artObject.artistULAN_URL) {
        // Add link if it exists
        Artist = () => <a
            className="text-md font-thin tracking-tight underline text-blue-600 visited:text-purple-600"
            target="_blank"
            rel="noreferrer"
            href={artObject.artistWikidata_URL !== "" ? artObject.artistWikidata_URL : artObject.artistULAN_URL}>
            {artObject.artistDisplayName}
        </a>;
    }

    return (
        <div className="flex w-full h-full shadow-md border rounded">
            <div className="w-1/4">
                <img className="aspect-square h-full w-full border rounded" src={artObject.primaryImageSmall} alt={artObject.title} />
            </div>
            <div className="w-3/4 pl-4 flex flex-col justify-between">
                <div>
                    <h3 className="text-xl font-semibold tracking-wide">{artObject.title}</h3>
                    <Artist />
                </div>
                <div className="py-2">
                    <p className="text-sm font-bold tracking-wide">{artObject.medium}</p>
                    <p className="text-xs font-thin tracking-tight">{artObject.objectDate}</p>
                </div>
            </div>
        </div>
    );
};
export default Component;