import React from "react";
import styled, { withTheme } from "styled-components";

const Section = styled.div`
  margin-bottom: ${(props) => props.theme.spacing.xl};
`;

const SectionTitle = styled.h3`
  margin: 0 0 10px 0;
  font-size: ${(props) => props.theme.fontSizes.lg};
  color: ${(props) => props.theme.colors.primary};
`;

const AttackCard = styled.div`
  background-color: ${(props) => props.theme.colors.lightGray};
  border-radius: ${(props) => props.theme.spacing.sm};
  padding: ${(props) => props.theme.spacing.md};
  margin-bottom: 10px;
`;

const AttackHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: ${(props) => props.theme.spacing.sm};
`;

const AttackName = styled.h4`
  margin: 0;
  font-size: ${(props) => props.theme.fontSizes.md};
  color: ${(props) => props.theme.colors.primary};
`;

const AttackDamage = styled.span`
  font-size: ${(props) => props.theme.fontSizes.lg};
  font-weight: bold;
  color: ${(props) => props.theme.colors.danger};
`;

const CostContainer = styled.div`
  display: flex;
  gap: ${(props) => props.theme.spacing.xs};
  margin-bottom: ${(props) => props.theme.spacing.sm};
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

const AttackEffect = styled.p`
  margin: 0;
  font-size: ${(props) => props.theme.fontSizes.sm};
  color: ${(props) => props.theme.colors.secondary};
  font-style: italic;
`;

// Helper function to get type color from theme
const getTypeColor = (type, theme) => {
  return theme.typeColors[type] || theme.typeColors.default;
};

const CardAttacks = ({ attacks, theme }) => {
  if (!attacks || attacks.length === 0) {
    return null;
  }

  return (
    <Section>
      <SectionTitle>Attacks</SectionTitle>
      {attacks.map((attack, index) => (
        <AttackCard key={index}>
          <AttackHeader>
            <AttackName>{attack.name}</AttackName>
            {attack.damage && <AttackDamage>{attack.damage}</AttackDamage>}
          </AttackHeader>
          {attack.cost && attack.cost.length > 0 && (
            <CostContainer>
              {attack.cost.map((cost, costIndex) => (
                <TypeBadge key={costIndex} bgColor={getTypeColor(cost, theme)}>
                  {cost}
                </TypeBadge>
              ))}
            </CostContainer>
          )}
          {attack.effect && <AttackEffect>{attack.effect}</AttackEffect>}
        </AttackCard>
      ))}
    </Section>
  );
};

export default withTheme(CardAttacks);
