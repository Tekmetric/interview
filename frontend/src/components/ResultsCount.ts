import styled from 'styled-components';

// Rendered even while empty (min-height) and marked aria-live by callers,
// so screen readers are told whenever the result set changes.
export const ResultsCount = styled.p`
  min-height: 1.5em;
  color: ${({ theme }) => theme.colors.textMuted};
  font-size: ${({ theme }) => theme.font.size.sm};
`;
