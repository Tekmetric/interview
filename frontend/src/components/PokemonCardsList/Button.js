import React from "react";
import styled from "styled-components";

const StyledButton = styled.button`
  padding: ${(props) => props.theme.spacing.sm}
    ${(props) => props.theme.spacing.lg};
  background-color: ${(props) =>
    props.disabled ? props.theme.colors.lightGray : props.theme.colors.primary};
  color: ${(props) =>
    props.disabled ? props.theme.colors.secondary : props.theme.colors.white};
  border: 1px solid
    ${(props) =>
      props.disabled ? props.theme.colors.border : props.theme.colors.primary};
  border-radius: ${(props) => props.theme.spacing.xs};
  font-size: ${(props) => props.theme.fontSizes.base};
  cursor: ${(props) => (props.disabled ? "not-allowed" : "pointer")};
  transition: all 0.2s ease;

  &:hover {
    background-color: ${(props) =>
      props.disabled ? props.theme.colors.lightGray : "#222"};
    transform: ${(props) => (props.disabled ? "none" : "translateY(-1px)")};
  }

  &:active {
    transform: ${(props) => (props.disabled ? "none" : "translateY(0)")};
  }
`;

const Button = ({ children, ...props }) => {
  return <StyledButton {...props}>{children}</StyledButton>;
};

export default Button;
