import React from "react";
import styled from "styled-components";

const WeaknessResistanceContainer = styled.div`
  display: flex;
  gap: ${(props) => props.theme.spacing.xl};
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const SubSectionTitle = styled.h4`
  margin-bottom: ${(props) => props.theme.spacing.sm};
  font-size: ${(props) => props.theme.fontSizes.base};
  color: ${(props) => props.theme.colors.primary};
`;

const WeaknessItem = styled.div`
  font-size: ${(props) => props.theme.fontSizes.xs};
  color: ${(props) => props.theme.colors.danger};
`;

const ResistanceItem = styled.div`
  font-size: ${(props) => props.theme.fontSizes.xs};
  color: ${(props) => props.theme.colors.success};
`;

const CardWeaknessResistance = ({ weaknesses, resistances }) => {
  const hasWeaknesses = weaknesses && weaknesses.length > 0;
  const hasResistances = resistances && resistances.length > 0;

  if (!hasWeaknesses && !hasResistances) {
    return null;
  }

  return (
    <WeaknessResistanceContainer>
      {hasWeaknesses && (
        <div>
          <SubSectionTitle>Weaknesses</SubSectionTitle>
          {weaknesses.map((weakness, index) => (
            <WeaknessItem key={index}>
              {weakness.type} {weakness.value}
            </WeaknessItem>
          ))}
        </div>
      )}

      {hasResistances && (
        <div>
          <SubSectionTitle>Resistances</SubSectionTitle>
          {resistances.map((resistance, index) => (
            <ResistanceItem key={index}>
              {resistance.type} {resistance.value}
            </ResistanceItem>
          ))}
        </div>
      )}
    </WeaknessResistanceContainer>
  );
};

export default CardWeaknessResistance;
