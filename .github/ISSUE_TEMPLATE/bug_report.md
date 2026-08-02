name: Bug Report
description: Create a report to help us improve THAMIS Lab
title: '[BUG]: '
labels: ['bug']
body:
  - type: textarea
    id: description
    attributes:
      label: Bug Description
      description: Clear and concise description of what the bug is.
    validations:
      required: true
  - type: textarea
    id: reproduction
    attributes:
      label: Steps To Reproduce
      description: Steps to reproduce the behavior.
    validations:
      required: true
