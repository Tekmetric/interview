import React from "react";
import ForkIcon from "./Icons/ForkIcon";
import StarIcon from "./Icons/StarIcon";
import WatcherIcon from "./Icons/WatcherIcon";

const dateTimeFormat = new Intl.DateTimeFormat("en-US", {
  year: "numeric",
  month: "long",
  day: "numeric",
});

const ResultList = ({ data }: { data: any }) => (
  <div className="grid gap-3 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
    {data.items.map((item: any) => (
      <a
        key={item.html_url}
        className="border p-2 rounded-md flex flex-col bg-indigo-50 text-blue-600 hover:text-blue-800 visited:text-purple-600"
        href={item.html_url}
        target="_blank"
        rel="noreferrer noopener"
      >
        <div className="flex gap-2">
          <div className="grow">{item.name}</div>
          <div
            className="text-sm text-slate-400 whitespace-nowrap"
            title="Watcher Count"
          >
            <WatcherIcon className="inline" />{" "}
            <span className="italic">{item.watchers}</span>
          </div>
          <div
            className="text-sm text-slate-400 whitespace-nowrap"
            title="Fork Count"
          >
            <ForkIcon className="inline" />{" "}
            <span className="italic">{item.forks_count}</span>
          </div>
          <div
            className="text-sm text-slate-400 whitespace-nowrap"
            title="Star Count"
          >
            <StarIcon className="inline" />{" "}
            <span className="italic">{item.stargazers_count}</span>
          </div>
        </div>
        <div className="grow py-2 text-sm italic text-slate-500">
          {item.description}
        </div>
        <div className="text-sm text-slate-400 italic">
          is a fork: {item.fork ? "yes" : "no"}
        </div>
        <div className="text-sm text-slate-400 italic">
          Created At: {dateTimeFormat.format(Date.parse(item.created_at))}
        </div>
      </a>
    ))}
  </div>
);

export default ResultList;
