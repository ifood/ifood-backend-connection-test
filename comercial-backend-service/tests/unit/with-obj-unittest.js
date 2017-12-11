/**
 * Created by trestini on 09/08/17.
 */

process.env.NODE_ENV = 'test';
let chai = require('chai');
let expect = chai.expect;

let withObj = require('../../app/helpers/with-obj.js').withObj;

describe('With Object you can...', () => {

  it('create a new object in a more verbose way', () => {
    let obj = withObj().get();
    expect(obj).not.be.undefined;
    expect(obj).to.be.empty;
  });

  it('add properties to a new object', () => {
    let obj = withObj()
      .add('x', 10)
      .add('y', 50)
      .get();

    expect(obj, "Don't have all required fields")
      .to.have.all.keys('x', 'y');
  });

  it('chainally handle a standard object', () => {
    let obj = withObj({ a:10, b:20 })
      .add('c', 30)
      .ren('a', 'aa')
      .del('b')
      .add('aa', 50)
      .get();

    expect(obj).not.be.undefined;
    expect(obj).not.to.be.empty;
    expect(obj).to.have.all.keys('aa', 'c');
    expect(obj.aa).to.be.equal(50);
  });

  it('add properties in a standard object', () => {
    let obj = { a:10 };
    let aux = withObj(obj)
      .add('b', 20)
      .get();
    expect(aux).not.be.undefined;
    expect(aux).not.to.be.empty;
    expect(aux).to.have.keys('a', 'b');
    expect(aux.b).to.be.equal(20);
  });

  it('add and override properties', () => {
    let aux = { a: 10 };
    let obj = withObj(aux)
      .add('a', 20)
      .get();
    expect(obj).not.be.undefined;
    expect(obj).not.to.be.empty;
    expect(obj).to.have.keys('a');
    expect(obj.a).to.be.equal(20);
  });

  it('remove properties to a new object', () => {
    let obj = withObj()
      .add('x', 10)
      .add('y', 50)
      .get();

    let aux = withObj(obj)
      .del('x')
      .get();

    expect(aux).to.have.keys('y');
    expect(aux).to.not.have.keys('x');

  });

  it('rename object properties', () => {
    let obj = withObj()
      .add('x', 10)
      .add('y', 50)
      .get();

    let aux = withObj(obj)
      .ren('x', 'xis')
      .get();

    expect(aux).to.have.any.keys('xis');
    expect(aux).to.not.have.keys('x');

  });

  it('merge two objects', () => {
    let obj = withObj()
      .add('x', 10)
      .add('y', 50)
      .get();

    let obj2 = withObj()
      .add('a', 25)
      .get();

    let aux = withObj(obj)
      .merge(obj2)
      .get();

    expect(aux).to.have.keys('a', 'x', 'y');

  });


});
