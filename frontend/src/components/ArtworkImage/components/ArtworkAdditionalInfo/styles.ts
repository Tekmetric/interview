import styled, { css } from 'styled-components'

import { ADDITIONAL_INFO_CLOSE_BUTTON_SIZE_PX } from '../../constants'

export const StyledArtworkAdditionalInfo = styled.div<{
  $visible: boolean
}>`
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20px calc(20px + ${ADDITIONAL_INFO_CLOSE_BUTTON_SIZE_PX}px / 2) 20px
    20px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(20px);
  transition: opacity 0.3s;
  color: var(--color-white);
  line-height: 2rem;
  opacity: 0;
  pointer-events: none;

  ${({ $visible }) =>
    $visible &&
    css`
      pointer-events: all;
      opacity: 1;
    `}
`
