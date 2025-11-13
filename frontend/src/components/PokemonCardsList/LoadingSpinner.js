import React from "react";
import styled, { keyframes } from "styled-components";

const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const LoadingContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
`;

const Spinner = styled.div`
  border: 4px solid ${(props) => props.theme.colors.lightGray};
  border-top: 4px solid ${(props) => props.theme.colors.primary};
  border-radius: 50%;
  width: 50px;
  height: 50px;
  animation: ${spin} 1s linear infinite;
`;

const LoadingMessage = styled.div`
  margin-top: ${(props) => props.theme.spacing.lg};
  font-size: ${(props) => props.theme.fontSizes.lg};
  color: ${(props) => props.theme.colors.primary};
  font-weight: 500;
`;

const LoadingSubtext = styled.div`
  margin-top: ${(props) => props.theme.spacing.sm};
  font-size: ${(props) => props.theme.fontSizes.sm};
  color: ${(props) => props.theme.colors.secondary};
  font-style: italic;
`;

const LoadingSpinner = () => {
  return (
    <LoadingContainer>
      <Spinner />
      <LoadingMessage>Searching for Pokemon cards...</LoadingMessage>
      <LoadingSubtext>This may take a moment</LoadingSubtext>
    </LoadingContainer>
  );
};

export default LoadingSpinner;
