import { useIntl } from 'react-intl';
import styled from 'styled-components';

import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { HeartIcon } from '../../components/icons';
import { favoriteToggled, selectIsFavorite, type FavoriteRef } from './favoritesSlice';

const Button = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: ${({ theme }) => theme.space.sm};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: ${({ theme }) => theme.colors.background};
  color: ${({ theme }) => theme.colors.statusDead};
  cursor: pointer;
  transition: background ${({ theme }) => theme.transition.fast};

  &:hover {
    background: ${({ theme }) => theme.colors.surfaceHover};
  }
`;

// Same toggle-button pattern as the theme switch: constant accessible name,
// state carried by aria-pressed.
export function FavoriteButton(props: FavoriteRef) {
  const intl = useIntl();
  const dispatch = useAppDispatch();
  const isFavorite = useAppSelector((state) => selectIsFavorite(state, props));

  return (
    <Button
      type="button"
      aria-pressed={isFavorite}
      aria-label={intl.formatMessage({ id: 'favorites.toggle' })}
      onClick={() => dispatch(favoriteToggled(props))}
    >
      <HeartIcon filled={isFavorite} />
    </Button>
  );
}
