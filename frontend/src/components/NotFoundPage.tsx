import { FormattedMessage } from 'react-intl';
import { Link } from 'react-router';
import styled from 'styled-components';

const Wrapper = styled.section`
  display: grid;
  gap: ${({ theme }) => theme.space.md};
  justify-items: start;
  padding: ${({ theme }) => theme.space.xl} 0;
`;

const BackLink = styled(Link)`
  color: ${({ theme }) => theme.colors.accent};
  font-weight: ${({ theme }) => theme.font.weight.medium};
`;

export function NotFoundPage() {
  return (
    <Wrapper>
      <h1>
        <FormattedMessage id="notFound.title" />
      </h1>
      <p>
        <FormattedMessage id="notFound.description" />
      </p>
      <BackLink to="/characters">
        <FormattedMessage id="notFound.backToCharacters" />
      </BackLink>
    </Wrapper>
  );
}
