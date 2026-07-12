import { FormattedMessage } from 'react-intl';
import styled, { keyframes } from 'styled-components';

import { VisuallyHidden } from './VisuallyHidden';

const spin = keyframes`
  to {
    transform: rotate(360deg);
  }
`;

const Wrapper = styled.div`
  display: grid;
  place-items: center;
  padding: ${({ theme }) => theme.space.xxl};
`;

// Two counter-rotating rings in portal green. The global reduced-motion rule
// collapses the animation for users who opt out.
const Ring = styled.div`
  width: 48px;
  height: 48px;
  border: 4px solid ${({ theme }) => theme.colors.portal};
  border-top-color: transparent;
  border-radius: 50%;
  animation: ${spin} 0.9s linear infinite;
`;

export function PortalSpinner() {
  return (
    <Wrapper role="status">
      <Ring aria-hidden="true" />
      <VisuallyHidden>
        <FormattedMessage id="common.loading" />
      </VisuallyHidden>
    </Wrapper>
  );
}
