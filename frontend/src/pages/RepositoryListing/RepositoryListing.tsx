import React, { useState } from "react";
import useRepoSearch from "../../data/hooks/use-repo-search";
import SearchPanel from "./components/SearchPanel";
import ResultPanel from "./components/ResultPanel";

const RepositoryListing = () => {
  const [query, setQuery] = useState<string | undefined>();
  const [pagination, setPagination] = useState({
    page: 1,
    perPage: 9,
  });

  const { loading, error, data } = useRepoSearch(
    {
      name: query,
    },
    pagination
  );

  const onSearch = (query: string) => {
    setPagination({ ...pagination, page: 1 });
    setQuery(query);
  };

  const hasNext =
    data && data.total_count > pagination.page * pagination.perPage;

  const onNextPage = () =>
    hasNext && setPagination({ ...pagination, page: pagination.page + 1 });

  const onPrevPage = () =>
    pagination.page > 1 &&
    setPagination({ ...pagination, page: pagination.page - 1 });

  return (
    <>
      <SearchPanel onSearch={onSearch} searching={loading} />
      <ResultPanel
        data={data}
        loading={loading}
        error={error}
        onNextPage={onNextPage}
        onPrevPage={onPrevPage}
        currentPage={pagination.page}
        hasNext={hasNext}
      />
    </>
  );
};

export default RepositoryListing;
