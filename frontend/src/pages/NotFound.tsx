import React from 'react';
import { Link } from 'react-router-dom';

import { ReactComponentI } from '../interfaces/components';

const NotFound: React.FC<ReactComponentI> = () => {
    return (
      <div content-center>
        Sorry, this page does not exist
        <Link to='/'>Back to home</Link>
      </div>
    );
};

export default NotFound;