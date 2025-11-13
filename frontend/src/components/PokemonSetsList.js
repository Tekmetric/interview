import React, { useState } from "react";
import { usePokemonSets } from "../hooks";

const PokemonSetsList = () => {
  const [searchName, setSearchName] = useState("");
  const [currentPage, setCurrentPage] = useState(1);

  const { sets, loading, error, refetch, totalCount, hasMore } = usePokemonSets(
    {
      name: searchName,
      page: currentPage,
      pageSize: 10,
      orderBy: "releaseDate",
    }
  );

  const handleSearch = (e) => {
    e.preventDefault();
    setCurrentPage(1); // Reset to first page when searching
  };

  const handleNextPage = () => {
    if (hasMore) {
      setCurrentPage((prev) => prev + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 1) {
      setCurrentPage((prev) => prev - 1);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "Unknown";
    return new Date(dateString).toLocaleDateString();
  };

  if (error) {
    return (
      <div style={{ padding: "20px" }}>
        <h2>Pokemon Sets</h2>
        <div style={{ color: "red", marginBottom: "10px" }}>Error: {error}</div>
        <button onClick={refetch}>Retry</button>
      </div>
    );
  }

  return (
    <div style={{ padding: "20px" }}>
      <h2>Pokemon Sets</h2>

      {/* Search Form */}
      <form onSubmit={handleSearch} style={{ marginBottom: "20px" }}>
        <input
          type="text"
          placeholder="Search sets by name..."
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
          style={{
            padding: "8px",
            marginRight: "10px",
            width: "200px",
            border: "1px solid #ccc",
            borderRadius: "4px",
          }}
        />
        <button type="submit" style={{ padding: "8px 16px" }}>
          Search
        </button>
        {searchName && (
          <button
            type="button"
            onClick={() => {
              setSearchName("");
              setCurrentPage(1);
            }}
            style={{ padding: "8px 16px", marginLeft: "10px" }}
          >
            Clear
          </button>
        )}
      </form>

      {/* Loading State */}
      {loading && (
        <div style={{ textAlign: "center", padding: "20px" }}>
          Loading sets...
        </div>
      )}

      {/* Results Info */}
      {!loading && (
        <div style={{ marginBottom: "20px", fontSize: "14px", color: "#666" }}>
          {totalCount > 0 ? (
            <>
              Showing {sets.length} of {totalCount} sets (Page {currentPage})
            </>
          ) : (
            "No sets found"
          )}
        </div>
      )}

      {/* Sets List */}
      {!loading && sets.length > 0 && (
        <div style={{ marginBottom: "20px" }}>
          {sets.map((set) => (
            <div
              key={set.id}
              style={{
                border: "1px solid #ddd",
                borderRadius: "8px",
                padding: "20px",
                marginBottom: "15px",
                backgroundColor: "#f9f9f9",
                display: "flex",
                alignItems: "flex-start",
                gap: "20px",
              }}
            >
              {set.images && set.images.logo && (
                <img
                  src={set.images.logo}
                  alt={`${set.name} logo`}
                  style={{
                    width: "80px",
                    height: "80px",
                    objectFit: "contain",
                    borderRadius: "4px",
                    backgroundColor: "white",
                    padding: "5px",
                  }}
                />
              )}
              <div style={{ flex: 1 }}>
                <h3 style={{ margin: "0 0 10px 0", fontSize: "18px" }}>
                  {set.name}
                </h3>
                <div
                  style={{
                    fontSize: "14px",
                    color: "#666",
                    marginBottom: "5px",
                  }}
                >
                  <strong>Series:</strong> {set.series || "Unknown"}
                </div>
                <div
                  style={{
                    fontSize: "14px",
                    color: "#666",
                    marginBottom: "5px",
                  }}
                >
                  <strong>Release Date:</strong> {formatDate(set.releaseDate)}
                </div>
                <div
                  style={{
                    fontSize: "14px",
                    color: "#666",
                    marginBottom: "5px",
                  }}
                >
                  <strong>Total Cards:</strong> {set.total || "Unknown"}
                </div>
                {set.ptcgoCode && (
                  <div
                    style={{
                      fontSize: "14px",
                      color: "#666",
                      marginBottom: "5px",
                    }}
                  >
                    <strong>PTCGO Code:</strong> {set.ptcgoCode}
                  </div>
                )}
                {set.legalities && (
                  <div
                    style={{
                      fontSize: "12px",
                      color: "#888",
                      marginTop: "10px",
                    }}
                  >
                    <strong>Legal in:</strong>{" "}
                    {Object.entries(set.legalities)
                      .filter(([, status]) => status === "Legal")
                      .map(([format]) => format)
                      .join(", ") || "None"}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Pagination */}
      {!loading && sets.length > 0 && (
        <div style={{ textAlign: "center", marginTop: "20px" }}>
          <button
            onClick={handlePrevPage}
            disabled={currentPage === 1}
            style={{
              padding: "8px 16px",
              marginRight: "10px",
              opacity: currentPage === 1 ? 0.5 : 1,
            }}
          >
            Previous
          </button>
          <span style={{ margin: "0 10px" }}>Page {currentPage}</span>
          <button
            onClick={handleNextPage}
            disabled={!hasMore}
            style={{
              padding: "8px 16px",
              marginLeft: "10px",
              opacity: !hasMore ? 0.5 : 1,
            }}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default PokemonSetsList;
