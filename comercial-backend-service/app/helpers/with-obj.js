module.exports.withObj = (obj, opts) => {

  return {

    opts: opts || {},
    innerObj: Object.assign({}, obj) || {},

    add(prop, value) {
      if (value !== undefined && value !== null) {
        this.innerObj[prop] = value;
      }
      return this;
    },

    ren(from, to) {
      if (this.innerObj[from]) {
        this.add(to, this.innerObj[from]).del(from);
      }
      return this;
    },

    del(...props) {
      props.forEach(p => {
        delete this.innerObj[p];
      });
      return this;
    },

    merge(ob) {
      Object.keys(ob).forEach(key => { this.add(key, ob[key]); }, this);
      return this;
    },

    get() {
      return this.innerObj;
    }
  };

};
