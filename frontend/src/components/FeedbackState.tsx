import { FormattedMessage } from 'react-intl';
import styled from 'styled-components';

const Wrapper = styled.section`
  display: grid;
  gap: ${({ theme }) => theme.space.sm};
  justify-items: center;
  padding: ${({ theme }) => theme.space.xxl} ${({ theme }) => theme.space.md};
  text-align: center;
  color: ${({ theme }) => theme.colors.textMuted};
`;

const Title = styled.p`
  font-size: ${({ theme }) => theme.font.size.lg};
  font-weight: ${({ theme }) => theme.font.weight.bold};
  color: ${({ theme }) => theme.colors.text};
`;

const RetryButton = styled.button`
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.lg}`};
  border: none;
  border-radius: ${({ theme }) => theme.radius.pill};
  background: ${({ theme }) => theme.colors.accent};
  color: ${({ theme }) => theme.colors.onAccent};
  font-weight: ${({ theme }) => theme.font.weight.medium};
  cursor: pointer;
`;

interface EmptyStateProps {
  titleId: string;
  descriptionId: string;
}

export function EmptyState({ titleId, descriptionId }: EmptyStateProps) {
  return (
    <Wrapper>
      <Title>
        <FormattedMessage id={titleId} />
      </Title>
      <p>
        <FormattedMessage id={descriptionId} />
      </p>
    </Wrapper>
  );
}

interface ErrorStateProps {
  onRetry: () => void;
}

export function ErrorState({ onRetry }: ErrorStateProps) {
  return (
    <Wrapper>
      <Title>
        <FormattedMessage id="error.title" />
      </Title>
      <p>
        <FormattedMessage id="error.description" />
      </p>
      <RetryButton type="button" onClick={onRetry}>
        <FormattedMessage id="common.retry" />
      </RetryButton>
    </Wrapper>
  );
}
