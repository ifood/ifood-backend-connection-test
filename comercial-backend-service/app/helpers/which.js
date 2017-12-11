module.exports.which = (obj) => {

  return {

    innerObj: Object.assign({}, obj) || {},

    isNull(...props){
      return props.filter(prop => {
        let aux = this.innerObj[prop];
        return aux === undefined
          || aux === null
          || (typeof aux === 'string' && aux === "");
      }) || [];
    }
  };

};
