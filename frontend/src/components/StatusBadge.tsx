import { FormattedMessage } from 'react-intl';
import styled from 'styled-components';

import type { CharacterStatus } from '../api/types';
import type { Palette } from '../theme/palettes';

const colorKeyByStatus: Record<CharacterStatus, keyof Palette> = {
  Alive: 'statusAlive',
  Dead: 'statusDead',
  unknown: 'statusUnknown',
};

const Badge = styled.span`
  display: inline-flex;
  align-items: center;
  gap: ${({ theme }) => theme.space.xs};
  font-size: ${({ theme }) => theme.font.size.sm};
  color: ${({ theme }) => theme.colors.textMuted};
`;

// Transient prop ($) so styled-components doesn't forward it to the DOM.
const Dot = styled.span<{ $status: CharacterStatus }>`
  width: 0.6em;
  height: 0.6em;
  border-radius: 50%;
  background: ${({ theme, $status }) => theme.colors[colorKeyByStatus[$status]]};
`;

// Status is conveyed by text, not just the colored dot (WCAG 1.4.1: never
// rely on color alone). The dot is decorative reinforcement.
export function StatusBadge({ status }: { status: CharacterStatus }) {
  return (
    <Badge>
      <Dot $status={status} aria-hidden="true" />
      <FormattedMessage id={`character.status.${status.toLowerCase()}`} />
    </Badge>
  );
}
