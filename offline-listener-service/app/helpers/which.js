module.exports.which = (obj) => {

  return {

    innerObj: (typeof obj === 'object') ? Object.assign({}, obj) || {} : obj,

    isNull(...props){
      return props.filter(prop => {
        let aux = this.innerObj[prop];
        return aux === undefined
          || aux === null
          || (typeof aux === 'string' && aux === "");
      }) || [];
    },

    isOneOf(...values){
      return (values
        .filter( vl => {
          return this.innerObj === vl;
        })
        .length > 0);
    }
  };

};
