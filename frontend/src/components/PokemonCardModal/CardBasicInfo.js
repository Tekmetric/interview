import React from "react";
import styled, { withTheme } from "styled-components";

const BasicInfo = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const InfoRow = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: ${(props) => props.theme.spacing.sm};
  flex-wrap: wrap;
  gap: 6px;
`;

const InfoLabel = styled.span`
  font-weight: bold;
  margin-right: ${(props) => props.theme.spacing.sm};
`;

const InfoValue = styled.span`
  font-size: ${(props) => props.fontSize || props.theme.fontSizes.base};
  color: ${(props) => props.color || props.theme.colors.secondary};
  font-weight: ${(props) => props.fontWeight || "normal"};
`;

const TypeBadge = styled.span`
  background-color: ${(props) => props.bgColor};
  color: ${(props) => props.theme.colors.white};
  padding: ${(props) => props.theme.spacing.xs}
    ${(props) => props.theme.spacing.sm};
  border-radius: ${(props) => props.theme.spacing.md};
  font-size: ${(props) => props.theme.fontSizes.xs};
  font-weight: bold;
`;

// Helper function to get type color from theme
const getTypeColor = (type, theme) => {
  return theme.typeColors[type] || theme.typeColors.default;
};

const CardBasicInfo = ({ card, theme }) => {
  const basicInfoFields = [
    {
      label: "Category",
      render: () => (
        <InfoValue>
          {card.category}
          {card.stage && ` • ${card.stage}`}
        </InfoValue>
      ),
      show: !!card.category,
    },
    {
      label: "HP",
      render: () => <InfoValue fontWeight="bold">{card.hp}</InfoValue>,
      show: !!card.hp,
    },
    {
      label: "Type",
      render: () =>
        card.types.map((type, index) => (
          <TypeBadge key={index} bgColor={getTypeColor(type, theme)}>
            {type}
          </TypeBadge>
        )),
      show: card.types && card.types.length > 0,
    },
    {
      label: "Evolves From",
      render: () => <InfoValue>{card.evolveFrom}</InfoValue>,
      show: !!card.evolveFrom,
    },
  ];

  return (
    <BasicInfo>
      {basicInfoFields.map(
        (field, index) =>
          field.show && (
            <InfoRow key={index}>
              <InfoLabel>{field.label}:</InfoLabel>
              {field.render()}
            </InfoRow>
          )
      )}
    </BasicInfo>
  );
};

export default withTheme(CardBasicInfo);
