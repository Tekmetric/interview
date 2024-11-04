import styled from 'styled-components'

import { ADDITIONAL_INFO_CLOSE_BUTTON_SIZE_PX } from '../../constants'

export const StyledCloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  width: ${ADDITIONAL_INFO_CLOSE_BUTTON_SIZE_PX}px;
  height: ${ADDITIONAL_INFO_CLOSE_BUTTON_SIZE_PX}px;
  background: transparent;
  padding: 0;
  display: flex;
  border: none;
  color: #fff;
  cursor: pointer;
`
