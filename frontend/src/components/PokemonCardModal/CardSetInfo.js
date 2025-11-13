import React from "react";
import styled from "styled-components";

const SetInfo = styled.div`
  border-top: 1px solid ${(props) => props.theme.colors.border};
  padding-top: ${(props) => props.theme.spacing.lg};
  font-size: ${(props) => props.theme.fontSizes.xs};
  color: ${(props) => props.theme.colors.secondary};
`;

const SetInfoRow = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.xs};
`;

const CardSetInfo = ({ card }) => {
  const setInfoFields = [
    {
      label: "Set",
      value: card.set && card.set.name ? card.set.name : "Unknown",
      show: true,
    },
    {
      label: "Card Number",
      value: card.localId,
      show: !!card.localId,
    },
    {
      label: "Artist",
      value: card.illustrator,
      show: !!card.illustrator,
    },
    {
      label: "Rarity",
      value: card.rarity,
      show: !!card.rarity,
    },
    {
      label: "Retreat Cost",
      value: card.retreat,
      show: card.retreat !== undefined && card.retreat !== null,
    },
    {
      label: "Pokédex #",
      value:
        card.dexId && card.dexId.length > 0 ? card.dexId.join(", ") : null,
      show: card.dexId && card.dexId.length > 0,
    },
  ];

  return (
    <SetInfo>
      {setInfoFields.map(
        (field, index) =>
          field.show && (
            <SetInfoRow key={index}>
              <strong>{field.label}:</strong> {field.value}
            </SetInfoRow>
          )
      )}
    </SetInfo>
  );
};

export default CardSetInfo;

