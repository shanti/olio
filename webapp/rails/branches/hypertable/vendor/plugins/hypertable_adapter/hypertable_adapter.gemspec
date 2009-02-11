# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = %q{hypertable_adapter}
  s.version = "0.1.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["tylerkovacs"]
  s.date = %q{2009-02-01}
  s.description = %q{Hypertable Adapter allows ActiveRecord to communicate with Hypertable.}
  s.email = %q{tyler.kovacs@gmail.com}
  s.files = ["VERSION.yml", "lib/active_record", "lib/active_record/connection_adapters", "lib/active_record/connection_adapters/hypertable_adapter.rb", "lib/active_record/connection_adapters/qualified_column.rb", "spec/spec_helper.rb", "spec/lib", "spec/lib/hypertable_adapter_spec.rb"]
  s.has_rdoc = true
  s.homepage = %q{http://github.com/tylerkovacs/hypertable_adapter}
  s.rdoc_options = ["--inline-source", "--charset=UTF-8"]
  s.require_paths = ["lib"]
  s.rubygems_version = %q{1.3.1}
  s.summary = %q{See README}

  if s.respond_to? :specification_version then
    current_version = Gem::Specification::CURRENT_SPECIFICATION_VERSION
    s.specification_version = 2

    if Gem::Version.new(Gem::RubyGemsVersion) >= Gem::Version.new('1.2.0') then
    else
    end
  else
  end
end
