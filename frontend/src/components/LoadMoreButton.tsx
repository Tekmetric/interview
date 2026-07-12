import { FormattedMessage } from 'react-intl';
import styled from 'styled-components';

const Row = styled.div`
  display: grid;
  place-items: center;
  padding: ${({ theme }) => theme.space.lg};
`;

const Button = styled.button`
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.xl}`};
  border: none;
  border-radius: ${({ theme }) => theme.radius.pill};
  background: ${({ theme }) => theme.colors.accent};
  color: ${({ theme }) => theme.colors.onAccent};
  font-weight: ${({ theme }) => theme.font.weight.medium};
  cursor: pointer;

  &:disabled {
    opacity: 0.6;
    cursor: progress;
  }
`;

interface LoadMoreButtonProps {
  labelId: string;
  loading: boolean;
  onClick: () => void;
}

export function LoadMoreButton({ labelId, loading, onClick }: LoadMoreButtonProps) {
  return (
    <Row>
      <Button type="button" onClick={onClick} disabled={loading}>
        <FormattedMessage id={loading ? 'common.loading' : labelId} />
      </Button>
    </Row>
  );
}
