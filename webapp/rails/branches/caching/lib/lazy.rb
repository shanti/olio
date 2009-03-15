
class LazyBastard
  %w{== === =~ class clone display dup eql? equal? extend freeze frozen? gem hash id inspect instance_eval instance_of? instance_variable_defined? instance_variable_get instance_variable_set instance_variables is_a? kind_of? method methods nil? object_id private_methods protected_methods public_methods require respond_to? send singleton_methods taint tainted? to_a to_s type untaint }.each do |m|
    module_eval <<-EOS
      def #{m}(*args, &block)
        self.resolve.__send__(:#{m}, *args, &block)
      end
    EOS
  end
  
  def initialize(&block)
    @block = block
  end
  
  def resolve
    # puts caller.join("\n") unless @obj
    @obj ||= @block.call
  end
    
  def method_missing(method, *args, &block)
    resolve.send(method, *args, &block)
  end
end

module Kernel
  def lazy(&block)
    LazyBastard.new(&block)
  end
end
