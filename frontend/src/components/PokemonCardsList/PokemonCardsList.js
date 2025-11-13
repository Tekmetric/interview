import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { usePokemonCards, usePokemonSets } from "../../hooks";
import PokemonCardModal from "../PokemonCardModal";
import SetSelector from "./SetSelector";
import SearchBar from "./SearchBar";
import LoadingSpinner from "./LoadingSpinner";
import ResultsInfo from "./ResultsInfo";
import CardsGrid from "./CardsGrid";
import Pagination from "./Pagination";

const Container = styled.div`
  padding: ${(props) => props.theme.spacing.xl};
`;

const Title = styled.h2`
  margin: 0 0 ${(props) => props.theme.spacing.xl} 0;
`;

const ErrorText = styled.div`
  color: ${(props) => props.theme.colors.danger};
`;

const RetryButton = styled.button`
  padding: ${(props) => props.theme.spacing.sm}
    ${(props) => props.theme.spacing.md};
`;

const PokemonCardsList = () => {
  const [searchName, setSearchName] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedCard, setSelectedCard] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedSet, setSelectedSet] = useState("");

  const CARDS_PER_PAGE = 20;

  const { sets, loading: setsLoading, error: setsError } = usePokemonSets();

  useEffect(
    () => {
      if (sets && sets.length > 0 && !selectedSet) {
        setSelectedSet(sets[0].id);
      }
    },
    [sets]
  );

  const { cards, loading, error, refetch } = usePokemonCards({
    name: searchName,
    set: selectedSet,
  });

  const totalPages = Math.ceil(cards.length / CARDS_PER_PAGE);
  const startIndex = (currentPage - 1) * CARDS_PER_PAGE;
  const endIndex = startIndex + CARDS_PER_PAGE;
  const paginatedCards = cards.slice(startIndex, endIndex);

  const handleSearch = (searchTerm) => {
    console.log({ searchTerm });
    setSearchName(searchTerm);
    setCurrentPage(1);
  };

  const handleCardClick = (card) => {
    setSelectedCard(card);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedCard(null);
  };

  const handlePreviousPage = () => {
    setCurrentPage((prev) => Math.max(prev - 1, 1));
  };

  const handleNextPage = () => {
    setCurrentPage((prev) => Math.min(prev + 1, totalPages));
  };

  if (error) {
    return (
      <Container>
        <Title>Pokemon Cards</Title>
        <ErrorText>Error: {error}</ErrorText>
        <RetryButton onClick={refetch}>Retry</RetryButton>
      </Container>
    );
  }

  const selectedSetName =
    (sets.find((s) => s.id === selectedSet) || {}).name || "";

  return (
    <Container>
      <Title>Pokemon Cards</Title>

      <SetSelector
        sets={sets}
        selectedSet={selectedSet}
        onSetChange={(value) => {
          setSelectedSet(value);
          setCurrentPage(1);
        }}
        loading={setsLoading}
        error={setsError}
      />

      <SearchBar
        searchName={searchName}
        onSearch={handleSearch}
        disabled={!selectedSet}
      />

      {(loading || setsLoading) && <LoadingSpinner />}

      {!loading && sets && sets.length > 0 && (
        <ResultsInfo
          totalCards={cards.length}
          searchName={searchName}
          selectedSetName={selectedSetName}
          currentPage={currentPage}
          totalPages={totalPages}
        />
      )}

      <CardsGrid cards={paginatedCards} onCardClick={handleCardClick} />

      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPrevious={handlePreviousPage}
        onNext={handleNextPage}
      />

      {selectedCard && (
        <PokemonCardModal
          card={selectedCard}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
        />
      )}
    </Container>
  );
};

export default PokemonCardsList;
