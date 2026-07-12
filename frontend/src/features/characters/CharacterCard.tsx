import { Link } from 'react-router';
import styled from 'styled-components';

import type { Character } from '../../api/types';
import { ImageWithRetry } from '../../components/ImageWithRetry';
import { StatusBadge } from '../../components/StatusBadge';
import { FavoriteButton } from '../favorites/FavoriteButton';

const Card = styled.article`
  position: relative;
  overflow: hidden;
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.lg};
  background: ${({ theme }) => theme.colors.surface};
  transition: border-color ${({ theme }) => theme.transition.fast};

  &:hover {
    border-color: ${({ theme }) => theme.colors.accent};
  }

  &:focus-within {
    border-color: ${({ theme }) => theme.colors.accent};
  }
`;

const Portrait = styled(ImageWithRetry)`
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
`;

const Body = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.space.xs};
  padding: ${({ theme }) => theme.space.md};
`;

const Name = styled.h2`
  font-size: ${({ theme }) => theme.font.size.md};
`;

// Stretched-link pattern: the name is the only link (one tab stop, real link
// semantics for screen readers) and its ::after expands to make the whole
// card clickable for pointer users.
const NameLink = styled(Link)`
  color: ${({ theme }) => theme.colors.text};
  text-decoration: none;

  &::after {
    content: '';
    position: absolute;
    inset: 0;
  }
`;

const Species = styled.p`
  font-size: ${({ theme }) => theme.font.size.sm};
  color: ${({ theme }) => theme.colors.textMuted};
`;

// Above the stretched link so the heart stays clickable on its own.
const FavoriteCorner = styled.div`
  position: absolute;
  top: ${({ theme }) => theme.space.sm};
  right: ${({ theme }) => theme.space.sm};
  z-index: 1;
`;

export function CharacterCard({ character }: { character: Character }) {
  return (
    <Card>
      <FavoriteCorner>
        <FavoriteButton entityType="characters" id={character.id} />
      </FavoriteCorner>
      {/* alt="" — the adjacent link already announces the name; repeating it
          in the image would double every card for screen reader users. */}
      <Portrait
        src={character.image}
        alt=""
        width={300}
        height={300}
        loading="lazy"
        decoding="async"
      />
      <Body>
        <Name>
          <NameLink to={`/characters/${character.id}`}>{character.name}</NameLink>
        </Name>
        <StatusBadge status={character.status} />
        <Species>{character.species}</Species>
      </Body>
    </Card>
  );
}
