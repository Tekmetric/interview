import React from "react";
import styled from "styled-components";
import TypeBadge from "./TypeBadge";

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
  margin-right: ${(props) => props.theme.spacing.xs};
`;

const InfoValue = styled.span`
  font-size: ${(props) => props.fontSize || props.theme.fontSizes.base};
  color: ${(props) => props.color || props.theme.colors.secondary};
  font-weight: ${(props) => props.fontWeight || "normal"};
`;

const CardBasicInfo = ({ card }) => {
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
        card.types.map((type, index) => <TypeBadge key={index} type={type} />),
      show: card.types && card.types.length > 0,
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

export default CardBasicInfo;
