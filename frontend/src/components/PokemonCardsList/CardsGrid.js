import React from "react";
import styled from "styled-components";
import PokemonCard from "../PokemonCard";

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: ${(props) => props.theme.spacing.xl};
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const CardsGrid = ({ cards, onCardClick }) => {
  if (!cards || cards.length === 0) {
    return null;
  }

  return (
    <Grid>
      {cards.map((card) => (
        <PokemonCard key={card.id} card={card} onClick={onCardClick} />
      ))}
    </Grid>
  );
};

export default CardsGrid;

