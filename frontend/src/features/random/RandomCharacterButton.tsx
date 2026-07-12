import { useIntl } from 'react-intl';
import { useNavigate } from 'react-router';
import styled from 'styled-components';

import { PortalIcon } from '../../components/icons';
import { useGetCharactersInfiniteQuery } from '../characters/api';

const Button = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: ${({ theme }) => theme.space.sm};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: transparent;
  color: ${({ theme }) => theme.colors.accent};
  cursor: pointer;
  transition:
    background ${({ theme }) => theme.transition.fast},
    transform ${({ theme }) => theme.transition.normal};

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &:hover:enabled {
    background: ${({ theme }) => theme.colors.surfaceHover};
    transform: rotate(180deg);
  }
`;

// "Surprise me": jump through a portal to a random character. The total
// count comes from the same cache entry the characters page uses, so this
// costs no extra request once that page has loaded (and pre-warms it
// otherwise). The hover spin is a transition, disabled globally under
// prefers-reduced-motion.
export function RandomCharacterButton() {
  const intl = useIntl();
  const navigate = useNavigate();
  const { data } = useGetCharactersInfiniteQuery({});
  const count = data?.pages[0]?.info.count;

  const openPortal = () => {
    if (!count) {
      return;
    }
    const id = 1 + Math.floor(Math.random() * count);
    void navigate(`/characters/${id}`);
  };

  return (
    <Button
      type="button"
      onClick={openPortal}
      disabled={!count}
      aria-label={intl.formatMessage({ id: 'random.button' })}
      title={intl.formatMessage({ id: 'random.button' })}
    >
      <PortalIcon />
    </Button>
  );
}
