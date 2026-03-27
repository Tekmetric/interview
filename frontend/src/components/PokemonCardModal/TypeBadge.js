import React from "react";
import styled, { withTheme } from "styled-components";

const Badge = styled.span`
  background-color: ${(props) => props.bgColor};
  color: ${(props) => props.theme.colors.white};
  padding: ${(props) => props.theme.spacing.xs}
    ${(props) => props.theme.spacing.sm};
  border-radius: ${(props) => props.theme.spacing.md};
  font-size: ${(props) => props.theme.fontSizes.xs};
  font-weight: bold;
`;

const getTypeColor = (type, theme) => {
  return theme.typeColors[type] || theme.typeColors.default;
};

const TypeBadge = ({ type, theme }) => {
  return <Badge bgColor={getTypeColor(type, theme)}>{type}</Badge>;
};

export default withTheme(TypeBadge);
