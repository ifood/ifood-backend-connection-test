/**
 * Unit test for the availability model
 */

import {expect} from 'chai'
import {model} from 'ember-test-utils/test-support/setup-test'
import {afterEach, beforeEach, describe, it} from 'mocha'
import sinon from 'sinon'

// To specify the other units that are required for this test:
// const test = model('availability', ['model:fizz-bang'])
const test = model('availability')
describe(test.label, function () {
  test.setup()

  let sandbox, instance

  beforeEach(function () {
    sandbox = sinon.sandbox.create()
    instance = this.subject()
  })

  afterEach(function () {
    sandbox.restore()
  })

  it('should exist', function () {
    expect(instance).not.to.equal(undefined)
  })

  it('should have real tests', function () {
    expect(true).to.equal(false)
  })
})
