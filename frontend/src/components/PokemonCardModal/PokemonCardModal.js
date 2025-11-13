import React from "react";
import styled from "styled-components";
import { usePokemonCardDetails } from "../../hooks";
import CardBasicInfo from "./CardBasicInfo";
import CardAttacks from "./CardAttacks";
import CardAbilities from "./CardAbilities";
import CardWeaknessResistance from "./CardWeaknessResistance";
import CardSetInfo from "./CardSetInfo";

// Styled Components
const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
`;

const ModalContent = styled.div`
  background-color: white;
  border-radius: 16px;
  width: 800px;
  max-height: 90vh;
  overflow: auto;
  position: relative;
`;

const CloseButton = styled.button`
  position: absolute;
  top: ${(props) => props.theme.spacing.lg};
  right: ${(props) => props.theme.spacing.lg};
  background: rgba(0, 0, 0, 0.5);
  color: ${(props) => props.theme.colors.white};
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  cursor: pointer;
  font-size: ${(props) => props.theme.fontSizes.lg};
  z-index: 1001;

  &:hover {
    background: rgba(0, 0, 0, 0.7);
  }
`;

const LoadingContainer = styled.div`
  padding: 40px;
  text-align: center;
`;

const LoadingText = styled.div`
  font-size: ${(props) => props.theme.fontSizes.lg};
  color: ${(props) => props.theme.colors.secondary};
`;

const ErrorContainer = styled.div`
  padding: 40px;
  text-align: center;
`;

const ErrorTitle = styled.div`
  font-size: ${(props) => props.theme.fontSizes.lg};
  color: ${(props) => props.theme.colors.danger};
  margin-bottom: 10px;
`;

const ErrorMessage = styled.div`
  font-size: ${(props) => props.theme.fontSizes.base};
  color: ${(props) => props.theme.colors.secondary};
`;

const CardDetailsContainer = styled.div`
  display: flex;
  flex-direction: row;
  gap: ${(props) => props.theme.spacing.xl};
  padding: ${(props) => props.theme.spacing.xl};
`;

const ImageSection = styled.div`
  flex: 0 0 300px;
`;

const CardImage = styled.img`
  width: 100%;
  height: auto;
  border-radius: ${(props) => props.theme.spacing.md};
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
`;

const DetailsSection = styled.div`
  flex: 1;
  min-width: 0;
`;

const CardName = styled.h2`
  margin: 0 0 ${(props) => props.theme.spacing.lg} 0;
  font-size: ${(props) => props.theme.fontSizes.xl};
  color: ${(props) => props.theme.colors.primary};
`;

const PokemonCardModal = ({ card, isOpen, onClose }) => {
  const { card: detailedCard, loading, error } = usePokemonCardDetails(
    card ? card.id : null,
    { enabled: isOpen && !!card }
  );

  if (!isOpen || !card) return null;

  const displayCard = detailedCard || card;
  const imageUrl = displayCard.image ? `${displayCard.image}/high.jpg` : "";

  return (
    <ModalOverlay onClick={onClose}>
      <ModalContent onClick={(e) => e.stopPropagation()}>
        <CloseButton onClick={onClose}>×</CloseButton>

        {loading && (
          <LoadingContainer>
            <LoadingText>Loading card details...</LoadingText>
          </LoadingContainer>
        )}

        {error && (
          <ErrorContainer>
            <ErrorTitle>Error loading card details</ErrorTitle>
            <ErrorMessage>{error}</ErrorMessage>
          </ErrorContainer>
        )}

        {!loading && !error && (
          <CardDetailsContainer>
            <ImageSection>
              <CardImage src={imageUrl} alt={displayCard.name} />
            </ImageSection>

            <DetailsSection>
              <CardName>{displayCard.name}</CardName>

              <CardBasicInfo card={displayCard} />

              <CardAttacks attacks={displayCard.attacks} />

              <CardAbilities abilities={displayCard.abilities} />

              <CardWeaknessResistance
                weaknesses={displayCard.weaknesses}
                resistances={displayCard.resistances}
              />

              <CardSetInfo card={displayCard} />
            </DetailsSection>
          </CardDetailsContainer>
        )}
      </ModalContent>
    </ModalOverlay>
  );
};

export default PokemonCardModal;
