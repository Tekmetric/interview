import React from "react";
import styled from "styled-components";

const CardContainer = styled.div`
  border: 1px solid ${(props) => props.theme.colors.border};
  border-radius: ${(props) => props.theme.spacing.md};
  padding: ${(props) => props.theme.spacing.lg};
  margin: 10px;
  background-color: ${(props) => props.theme.colors.white};
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 15px rgba(0, 0, 0, 0.2);
  }
`;

const ImageContainer = styled.div`
  text-align: center;
  margin-bottom: 10px;
`;

const CardImage = styled.img`
  width: 100%;
  max-width: 200px;
  height: auto;
  border-radius: ${(props) => props.theme.spacing.sm};
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const CardTitle = styled.h3`
  margin: 0 0 ${(props) => props.theme.spacing.sm} 0;
  font-size: ${(props) => props.theme.fontSizes.lg};
  font-weight: bold;
  color: ${(props) => props.theme.colors.primary};
  text-align: center;
`;

const PokemonCard = ({ card, onClick }) => {
  const imageUrl = card.image ? `${card.image}/low.jpg` : "";

  return (
    <CardContainer onClick={() => onClick(card)}>
      <ImageContainer>
        <CardImage
          src={imageUrl}
          alt={card.name}
          onError={(e) => {
            e.target.src = "";
          }}
        />
      </ImageContainer>
      <CardTitle>{card.name}</CardTitle>
    </CardContainer>
  );
};

export default PokemonCard;
