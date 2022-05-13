import classNames from 'classnames';

import {
  CCard, 
  CCardImgWrapper, 
  CCardImg, 
  CCardContentWrapper,
} from './StyledWidgets';

export default function CharacterCard(props) {
  const data = props.data;
  const getClassNameByStatus = (status) => classNames({
    'status-icon': true,
    'status-icon-dead': status.toLowerCase() === 'dead',
    'status-icon-alive': status.toLowerCase() === 'alive',
  })

  return (
    <CCard>
      <CCardImgWrapper>
        <CCardImg src={data.image} />
      </CCardImgWrapper>
      <CCardContentWrapper>
        <div className='section'>
          <a href={data.url} target='_blank'><h2>#{data.id}&nbsp;{data.name}</h2></a>
          <span class='status'>
            <div className={getClassNameByStatus(data.status)}/>
            {`${data.status} - ${data.species}`}
          </span>
        </div>
        <div className='section'>
          <span class="text-gray">Last known location:</span>
          <a href={data.location.url} target='_blank'>{data.location.name}</a>
        </div>
      </CCardContentWrapper>
    </CCard>
  );
}