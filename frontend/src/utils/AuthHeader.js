const GetAuthHeader = () => {
  const user = JSON.parse(localStorage.getItem('user'));

  if (user && user.token) {
    return {
      Authorization: `Bearer ${user.token}`,
      'Content-Type': 'application/json',
    };
  }
  return {};
};

export default GetAuthHeader;
