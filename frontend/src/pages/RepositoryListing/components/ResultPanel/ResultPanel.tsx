import React from "react";
import ResultList from "../ResultList";

interface ResultPanelParams {
  data: any;
  error: any;
  loading: boolean;
  onNextPage: () => void;
  onPrevPage: () => void;
  currentPage: number;
  hasNext: boolean;
}

const ResultPanel = ({
  data,
  error,
  loading,
  onNextPage,
  onPrevPage,
  currentPage,
  hasNext,
}: ResultPanelParams) => {
  return (
    <div className="border p-4">
      {error && <p>Error!</p>}
      {loading && !data && <p>Loading...</p>}
      <div className="min-h-[20vh]">
        {data && data.items && (
          <>
            <div className={loading ? "animate-pulse" : ""}>
              <ResultList data={data} />
            </div>
            <div className="border-t mt-4 pt-4">
              <div className="flex gap-2">
                <button
                  onClick={onPrevPage}
                  disabled={currentPage <= 1}
                  className="px-4 py-1 text-sm text-slate-50 rounded-full border bg-blue-600 hover:bg-blue-800 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Prev
                </button>
                <button
                  onClick={onNextPage}
                  disabled={!hasNext}
                  className="px-4 py-1 text-sm text-slate-50 rounded-full border bg-blue-600 hover:bg-blue-800 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Next
                </button>
                <div className="border border-gray-300 rounded-lg px-2">
                  Current Page: {currentPage}
                </div>
                <div className="border border-gray-300 rounded-lg px-2">
                  {data.total_count || 0} results found.
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ResultPanel;
