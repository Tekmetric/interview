import React from "react";
import styled from "styled-components";

const Section = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const SectionTitle = styled.h3`
  margin-bottom: 10px;
  font-size: ${(props) => props.theme.fontSizes.lg};
  color: ${(props) => props.theme.colors.primary};
`;

const AbilityCard = styled.div`
  background-color: ${(props) => props.theme.colors.warningBg};
  border-left: 4px solid ${(props) => props.theme.colors.warningBorder};
  border-radius: ${(props) => props.theme.spacing.xs};
  padding: ${(props) => props.theme.spacing.md};
  margin-bottom: 10px;
`;

const AbilityHeader = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.sm};
`;

const AbilityType = styled.span`
  font-size: ${(props) => props.theme.fontSizes.xs};
  font-weight: bold;
  color: ${(props) => props.theme.colors.warning};
  text-transform: uppercase;
  margin-right: ${(props) => props.theme.spacing.sm};
`;

const AbilityName = styled.h4`
  margin: 0;
  font-size: ${(props) => props.theme.fontSizes.md};
  color: ${(props) => props.theme.colors.primary};
  display: inline;
`;

const AbilityEffect = styled.p`
  margin: 0;
  font-size: ${(props) => props.theme.fontSizes.sm};
  color: ${(props) => props.theme.colors.secondary};
`;

const CardAbilities = ({ abilities }) => {
  if (!abilities || abilities.length === 0) {
    return null;
  }

  return (
    <Section>
      <SectionTitle>Abilities</SectionTitle>
      {abilities.map((ability, index) => (
        <AbilityCard key={index}>
          <AbilityHeader>
            <AbilityType>{ability.type}:</AbilityType>
            <AbilityName>{ability.name}</AbilityName>
          </AbilityHeader>
          {ability.effect && <AbilityEffect>{ability.effect}</AbilityEffect>}
        </AbilityCard>
      ))}
    </Section>
  );
};

export default CardAbilities;
